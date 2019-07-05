# To install PyEDA, use the command "pip3 install pyeda" (or "pip install pyeda")
import pyeda
import sys
from pyeda.inter import *
# from command line argument (may give buffer exception if formula is too long)
#f, = espresso_exprs(expr(sys.argv[1]).to_dnf())
# as input stream
f, = espresso_exprs(expr(sys.stdin.readlines()).to_dnf())
print(f)
