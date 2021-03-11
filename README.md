# Hasm - The Hack Assembler

Hasm is an assembler for the Hack virtual computer built in the [nand2tetris](https://www.nand2tetris.org/) course written in Java.

It takes a Hack assembly file as input and produces a .hack file with the machine codes based on the Hack specification.

Some things to keep in mind:

* Since there are no / symbols used in the language specification, comments, which are generally of the form //, will work even if only a single / is used.
* There is very minimal syntax checking. It will throw a generic Exception if it does find illegal syntax or invalid comp operations.
* White space is ignored. So D=M+1 is the same as D    =         M+     1
* The system can only handle 15-bit integer values. If you put in a number that is a legal Java int but more than 15-bits, your value will be truncated and only the least signifcant 15-bits will stored. You will be warned if this happens.
* This code, while commented, is not commented well. I didn't use proper class and method comments. I don't use Java much so this was a chance for me to learn the language as I go. Don't expect this to be optimized or professional quality in any way, but it should work.
* Feel free to use it, abuse it, distribute it, change it, or whatever you want to do, but I make no guarantee this won't fry your hard drive and blow up your smart toaster if you use it. It's purely for educational purposes.
