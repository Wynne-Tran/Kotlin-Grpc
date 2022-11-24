/*
 * Copyright 2019, gRPC Authors All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import struct Foundation.UUID

#if swift(>=5.6)
@preconcurrency import Logging
@preconcurrency import NIOCore
#else
import Logging
import NIOCore
#endif // swift(>=5.6)

import NIOHPACK
import NIOHTTP1
import NIOHTTP2

/// Options to use for GRPC calls.
public struct CallOptions: GRPCSendable {
  /// Additional metadata to send to the service.
  public var customMetadata: HPACKHeaders

  /// The time limit for the RPC.
  ///
  /// - Note: timeouts are treated as deadlines as soon as an RPC has been invoked.
  public var timeLimit: TimeLimit

  /// The compression used for requests, and the compression algorithms to advertise as acceptable
  /// for the remote peer to use for encoding responses.
  ///
  /// Compression may also be disabled at the message-level for streaming requests (i.e. client
  /// streaming and bidirectional streaming RPCs) by setting `compression` to `.disabled` in
  /// `sendMessage(_:compression)`, `sendMessage(_:compression:promise)`,
  /// `sendMessages(_:compression)` or `sendMessages(_:compression:promise)`.
  ///
  /// Note that enabling `compression` via the `sendMessage` or `sendMessages` methods only applies
  /// if encoding has been specified in these options.
  public var messageEncoding: ClientMessageEncoding

  /// Whether the call is cacheable.
  public var cacheable: Bool

  /// How IDs should be provided for requests. Defaults to `.autogenerated`.
  ///
  /// The request ID is used for logging and will be added to the headers of a call if
  /// `requestIDHeader` is specified.
  ///
  /// - Important: When setting `CallOptions` at the client level, `.userDefined` should __not__ be
  /// used otherwise each request will have the same ID.
  public var requestIDProvider: RequestIDProvider

  /// The name of the header to use when adding a request ID to a call, e.g. "x-request-id". If the
  /// value is `nil` (the default) then no additional header will be added.
  ///
  /// Setting this value will add a request ID to the headers of the call these options are used
  /// with. The request ID will be provided by `requestIDProvider` and will also be used in log
  /// messages associated with the call.
  public var requestIDHeader: String?

  /// A preference for the `EventLoop` that the call is executed on.
  ///
  /// The `EventLoop` resulting from the preference will be used to create any `EventLoopFuture`s
  /// associated with the call, such as the `response` for calls with a single response (i.e. unary
  /// and client streaming). For calls which stream responses (server streaming and bidirectional
  /// streaming) the response handler is executed on this event loop.
  ///
  /// Note that the underlying connection is not guaranteed to run on the same event loop.
  public var eventLoopPreference: EventLoopPreference

  /// A logger used for the call. Defaults to a no-op logger.
  ///
  /// If a `requestIDProvider` exists then a request ID will automatically attached to the logger's
  /// metadata using the 'grpc-request-id' key.
  public var logger: Logger

  public init(
    customMetadata: HPACKHeaders = HPACKHeaders(),
    timeLimit: TimeLimit = .none,
    messageEncoding: ClientMessageEncoding = .disabled,
    requestIDProvider: RequestIDProvider = .autogenerated,
    requestIDHeader: String? = nil,
    cacheable: Bool = false,
    logger: Logger = Logger(label: "io.grpc", factory: { _ in SwiftLogNoOpLogHandler() })
  ) {
    self.init(
      customMetadata: customMetadata,
      timeLimit: timeLimit,
      messageEncoding: messageEncoding,
      requestIDProvider: requestIDProvider,
      requestIDHeader: requestIDHeader,
      eventLoopPreference: .indifferent,
      cacheable: cacheable,
      logger: logger
    )
  }

  public init(
    customMetadata: HPACKHeaders = HPACKHeaders(),
    timeLimit: TimeLimit = .none,
    messageEncoding: ClientMessageEncoding = .disabled,
    requestIDProvider: RequestIDProvider = .autogenerated,
    requestIDHeader: String? = nil,
    eventLoopPreference: EventLoopPreference,
    cacheable: Bool = false,
    logger: Logger = Logger(label: "io.grpc", factory: { _ in SwiftLogNoOpLogHandler() })
  ) {
    self.customMetadata = customMetadata
    self.messageEncoding = messageEncoding
    self.requestIDProvider = requestIDProvider
    self.requestIDHeader = requestIDHeader
    self.cacheable = cacheable
    self.timeLimit = timeLimit
    self.logger = logger
    self.eventLoopPreference = eventLoopPreference
  }
}

extension CallOptions {
  public struct RequestIDProvider: GRPCSendable {
    #if swift(>=5.6)
    public typealias RequestIDGenerator = @Sendable () -> String
    #else
    public typealias RequestIDGenerator = () -> String
    #endif // swift(>=5.6)

    private enum RequestIDSource: GRPCSendable {
      case none
      case `static`(String)
      case generated(RequestIDGenerator)
    }

    private var source: RequestIDSource
    private init(_ source: RequestIDSource) {
      self.source = source
    }

    @usableFromInline
    internal func requestID() -> String? {
      switch self.source {
      case .none:
        return nil
      case let .static(requestID):
        return requestID
      case let .generated(generator):
        return generator()
      }
    }

    /// No request IDs are generated.
    public static let none = RequestIDProvider(.none)

    /// Generate a new request ID for each RPC.
    public static let autogenerated = RequestIDProvider(.generated({ UUID().uuidString }))

    /// Specify an ID to be used.
    ///
    /// - Important: this should only be used when `CallOptions` are passed directly to the call.
    ///   If it is used for the default options on a client then all calls with have the same ID.
    public static func userDefined(_ requestID: String) -> RequestIDProvider {
      return RequestIDProvider(.static(requestID))
    }

    /// Provide a factory to generate request IDs.
    public static func generated(
      _ requestIDFactory: @escaping RequestIDGenerator
    ) -> RequestIDProvider {
      return RequestIDProvider(.generated(requestIDFactory))
    }
  }
}

extension CallOptions {
  public struct EventLoopPreference: GRPCSendable {
    /// No preference. The framework will assign an `EventLoop`.
    public static let indifferent = EventLoopPreference(.indifferent)

    /// Use the provided `EventLoop` for the call.
    public static func exact(_ eventLoop: EventLoop) -> EventLoopPreference {
      return EventLoopPreference(.exact(eventLoop))
    }

    @usableFromInline
    internal enum Preference: GRPCSendable {
      case indifferent
      case exact(EventLoop)
    }

    @usableFromInline
    internal var _preference: Preference

    @inlinable
    internal init(_ preference: Preference) {
      self._preference = preference
    }
  }
}

extension CallOptions.EventLoopPreference {
  @inlinable
  internal var exact: EventLoop? {
    switch self._preference {
    case let .exact(eventLoop):
      return eventLoop
    case .indifferent:
      return nil
    }
  }
}
