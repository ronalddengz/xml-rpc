import sys
import xmlrpc.client

SERVER_URL = "http://localhost:8008"

def main():
    command = sys.argv[1]
    value = " ".join(sys.argv[2:])

    server = xmlrpc.client.ServerProxy(SERVER_URL)

    try:
        if command == "search":
            result = server.bookstore.search(value)
        elif command == "lookup":
            item_number = int(value)
            result = server.bookstore.lookup(item_number)
        elif command == "buy":
            item_number = int(value)
            result = server.bookstore.buy(item_number)
        else:
            print("Unknown command: " + command)
            return

        print(result)
    except ValueError:
        print("Item number must be a whole number.")
    except xmlrpc.client.ProtocolError as error:
        print("Server returned an error: " + str(error))
    except xmlrpc.client.Fault as error:
        print("XML-RPC error: " + error.faultString)
    except OSError as error:
        print("Could not connect to the server: " + str(error))

if __name__ == "__main__":
    main()