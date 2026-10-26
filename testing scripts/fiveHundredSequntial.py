import sys
import time
import xmlrpc.client

SERVER_URL = sys.argv[1] if len(sys.argv) > 1 else "http://localhost:8008"
SEARCH_TERM = sys.argv[2] if len(sys.argv) > 2 else 12365
REQUEST_COUNT = 500
OUTPUT_FILE = "fiveHundredSequentialBuyResults.txt"


def main():
	server = xmlrpc.client.ServerProxy(SERVER_URL)

	start_time = time.perf_counter()
	for _ in range(REQUEST_COUNT):
		server.bookstore.buy(SEARCH_TERM)
	elapsed_time = time.perf_counter() - start_time
	time_per_response = elapsed_time / REQUEST_COUNT

	report = (
		f"Server URL: {SERVER_URL}\n"
		f"Search term: {SEARCH_TERM}\n"
		f"Responses: {REQUEST_COUNT}\n"
		f"Total response time: {elapsed_time:.6f} seconds\n"
		f"Average time per response: {time_per_response:.6f} seconds\n"
	)

	with open(OUTPUT_FILE, "w", encoding="utf-8") as output:
		output.write(report)

	print(report, end="")
	print(f"Results saved to {OUTPUT_FILE}")


if __name__ == "__main__":
	main()
