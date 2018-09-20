package utils

class Foo(msg: String) {
    def hello: String = "Hello"

    def getMsg = "{ 'status': 'OK', 'message': '" + msg + "'}"
}
