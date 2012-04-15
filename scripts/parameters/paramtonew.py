#!/usr/bin/env python

import os
import re
import sys

if len(sys.argv) != 2:
	print "Converts old type of parameter file to new and creates a backup."
	print "usage: ./paramtonew parameters_file.txt"
	sys.exit(1)

fname = sys.argv[1]

lines = open(fname,'r').readlines()

p = re.compile('(.*) = (.*)')

params = {}
names = []

for line in lines:
	m = p.match(line)
	if(m != None):
		if not m.group(1) in params:
			params[m.group(1)] = 1
		else:
			params[m.group(1)] = params[m.group(1)] + 1 


os.rename(fname, fname + "~")

fw = open(fname,"w")

for line in lines:
	if line == "FIXED:\n":
		fw.write("# FIXED:\n")
		continue
	if line == "CHANGING:\n":
		fw.write("# CHANGING:\n")
		continue
	m = p.match(line)
	if(m != None):
		par = m.group(1)
		val = m.group(2)
		if params[par] > 1:
			params[par] = params[par] - 1
		else:			
			try:
				float(val)
				conv = True
			except ValueError:
				conv = False
			if (val != "true") and (val != "false") and (not conv):
				val = "\"" + val + "\""
			fw.write(par + " = " + val + "\n")

fw.close()
	