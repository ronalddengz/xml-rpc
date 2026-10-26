import sys
import xmlrpc.client

SERVER_URL = sys.argv[1] if len(sys.argv) > 1 else "http://localhost:8008"

def run_command(server, command, value):
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
    server = xmlrpc.client.ServerProxy(SERVER_URL)
    command = None
    value = -1

    while True:
        commandString = input("Command: ")
        commandString = commandString.split(" ")
        command = commandString[0]
        if len(commandString) > 1:
            value = ' '.join(commandString[1:])
        else:
            value = -1
        if command == "exit":
            sys.exit(1)
        
        run_command(server, command, value=value)
    