import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class client {
	private static final String DEFAULT_SERVER_URL = "http://localhost:8008";

	private static void runCommand(XmlRpcClient server, String command, String value,
			boolean hasValue) {
		try {
			Object result;
			switch (command) {
				case "search":
					result = server.execute("bookstore.search",
							new Object[] {hasValue ? value : Integer.valueOf(-1)});
					break;
				case "lookup":
					result = server.execute("bookstore.lookup",
							new Object[] {Integer.valueOf(value)});
					break;
				case "buy":
					result = server.execute("bookstore.buy",
							new Object[] {Integer.valueOf(value)});
					break;
				default:
					System.out.println("Unknown command: " + command);
					return;
			}
			System.out.println(result);
		} catch (NumberFormatException exception) {
			System.out.println("Item number must be a whole number.");
		} catch (XmlRpcException exception) {
			if (exception.getCause() instanceof IOException) {
				System.out.println("Could not connect to the server: " + exception.getCause());
			} else {
				System.out.println("XML-RPC error: " + exception.getMessage());
			}
		}
	}

	public static void main(String[] args) {
		String serverUrl = args.length > 0 ? args[0] : DEFAULT_SERVER_URL;
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		try {
			config.setServerURL(new URL(serverUrl));
		} catch (MalformedURLException exception) {
			System.out.println("Could not connect to the server: " + exception);
			return;
		}

		XmlRpcClient server = new XmlRpcClient();
		server.setConfig(config);

		try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
			while (true) {
				System.out.print("Command: ");
				String commandString = input.readLine();
				if (commandString == null) {
					return;
				}

				String[] commandParts = commandString.split(" ", -1);
				String command = commandParts[0];
				boolean hasValue = commandParts.length > 1;
				String value = hasValue
						? String.join(" ", Arrays.copyOfRange(commandParts, 1, commandParts.length))
						: "-1";

				if (command.equals("exit")) {
					System.exit(1);
				}
				runCommand(server, command, value, hasValue);
			}
		} catch (IOException exception) {
			System.out.println("Could not read command: " + exception);
		}
	}
}
