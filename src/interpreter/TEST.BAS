a = 2

REPEAT
	b = 2

	REPEAT
		PRINT a, " * ", b, " = ", a*b
		b = b + 1
	UNTIL b > 9

	a = a + 1
	PRINT ""
UNTIL a > 9

end