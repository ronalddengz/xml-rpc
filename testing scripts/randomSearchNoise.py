import multiprocessing
import random
import sys
import time
import xmlrpc.client
from urllib.parse import urlsplit

SERVER_URL = "http://localhost:8008"
SEARCH_TERM = 12365
PROCESS_COUNT = 3
DURATION_SECONDS = 30
MIN_WAIT_SECONDS = 0.00001
MAX_WAIT_SECONDS = 0.00003


def send_search_requests(server_url, search_term, deadline, worker_number):
    try:
        with xmlrpc.client.ServerProxy(server_url) as server:
            while time.monotonic() < deadline:
                server.bookstore.buy(search_term)
                time.sleep(random.uniform(MIN_WAIT_SECONDS, MAX_WAIT_SECONDS))
    except (OSError, xmlrpc.client.ProtocolError, xmlrpc.client.Fault) as error:
        print(f"Worker {worker_number} stopped: {error}", file=sys.stderr)


def main():
    server_url = sys.argv[1] if len(sys.argv) > 1 else SERVER_URL
    search_term = sys.argv[2] if len(sys.argv) > 2 else SEARCH_TERM

    try:
        parsed_url = urlsplit(server_url)
    except ValueError as error:
        print(f"Invalid server URL: {error}", file=sys.stderr)
        return 2

    if parsed_url.hostname not in {"localhost", "127.0.0.1", "::1"}:
        print("Noise generation is limited to a local server URL.", file=sys.stderr)
        return 2

    deadline = time.monotonic() + DURATION_SECONDS
    workers = [
        multiprocessing.Process(
            target=send_search_requests,
            args=(server_url, search_term, deadline, worker_number),
        )
        for worker_number in range(1, PROCESS_COUNT + 1)
    ]

    for worker in workers:
        worker.start()

    try:
        for worker in workers:
            worker.join()
    except KeyboardInterrupt:
        for worker in workers:
            if worker.is_alive():
                worker.terminate()
        for worker in workers:
            worker.join()
        return 130

    return 0


if __name__ == "__main__":
    raise SystemExit(main())