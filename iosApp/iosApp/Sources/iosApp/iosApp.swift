@main
public struct iosApp {
    public private(set) var text = "Hello, World!"

    public static func main() {
        print(iosApp().text)
    }
}
