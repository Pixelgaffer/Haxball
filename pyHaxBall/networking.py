from supersocket import SuperSocket

from socket import *

import time

ip = "10.0.4.34"
port = 1234
name = "deineMudda_lel"

def net_cb(data):
	print("recved:", data)



sock = socket()
sock.connect((ip, port))
supersock = SuperSocket(sock)
supersock.listen(net_cb)
supersock.send(chr(2))
supersock.send(chr(len(name.encode('utf-8'))))
supersock.send(name)
supersock.flush()
supersock.send("yay")

while True:
	time.sleep(1)