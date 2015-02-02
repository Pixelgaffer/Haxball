from supersocket import SuperSocket

from socket import *

import json

import time


class Net:
	serverInitialized = False
	started = False
	width, height, id = [None]*3
	def __init__(self, ip="10.0.4.34", port=1234, name="deineMudda_lel"):
		self.sock = socket()
		self.sock.connect((ip, port))
		self.ssock = SuperSocket(self.sock)
		self.ssock.listen(self.recv)
		self.ssock.send(chr(2))
		self.ssock.send(chr(len(name.encode('utf-8'))))
		self.ssock.send(name)
		self.ssock.flush()

	def recv(self, data):
		print('recved', data)
		d = json.loads(data)
		print(d)
		if 'id' in d:
			self.id = d['id']
		if 'width' in d:
			self.width = d['width']
		if 'height' in d:
			self.height = d['height']

		if self.width is not None and self.height is not None and self.id is not None:
			self.serverInitialized = True

if __name__ == '__main__':
	Net()
	while True:
		time.sleep(1)