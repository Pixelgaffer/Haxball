
from vec2d import Vec2d
from curses import wrapper

#player = """
#/#\\
#\\#/"""[1:]



playerSprite = """
/#\\
\\#/"""[1:]

ballSprite = """
/X\\
\\X/"""[1:]

MSG = "lel"




class Board:
	def __init__(self, scr):
		self.scr = scr
		self.sizex, self.sizey = 10, 10
		self.map = [[0 for ii in range(4)] for i in range(4)]
		self.players = [ (Vec2d(10, 10)),
						 (Vec2d(15, 15)),
						 (Vec2d(5,  5)),
						 (Vec2d(120, 120)) ]

	def normPos(self, y, x):
		return max(0, min(self.sizey-1, int(y))), max(0, min(self.sizex-1, int(x)))

	def drawUI(self):
		pass#self.scr.addstr(0, 0, "Status: " + str(1))

	def drawEntities(self):
		offset = self.middle - self.players[0]
		for p in self.players:
			pPos = p + offset
			self.drawSprite(pPos.y, pPos.x, playerSprite)

	def drawSprite(self, y, x, sprite):
		for i, line in enumerate(sprite.splitlines()):
			y0, x0 = self.normPos(y + i, x)
			x0 = min(x0, self.sizex-(len(line)+1))
			self.scr.addstr(y0, x0, line)

	def log(self, data):
		self.scr.addstr(0, 0, "{}".format(data))
		self.scr.refresh()
		self.scr.getkey()


	def update(self):
		self.sizey, self.sizex = self.scr.getmaxyx()
		self.size = Vec2d(self.sizex, self.sizey)
		self.middle = self.size/2
		self.drawUI()
		self.drawEntities()
		self.scr.refresh()

	def updatePlayers(self, l):
		pass



def main(stdscr):
	# Clear screen
	stdscr.clear()
	my, mx = stdscr.getmaxyx()
	s = str(mx)+"x"+str(my)
	stdscr.addstr(0, mx-(len(s)+1), s)

	b = Board(stdscr)
	while True:
		b.update()
		stdscr.refresh()
		stdscr.getkey()

try:
	wrapper(main)
finally:
	print(MSG)
