GameBatch has a simple syntax that couldn't be simpler (Because of Batch's limitations)!

## The 2 server builds
GameBatch has 2 main server builds, one (```gamebatch simpleinit```) is used for storing numbers in variables and performing arithmetic operations on them.

The other is the Game build (```gamebatch advinit```) is used for creating games that use sprites, playing sounds and other things.  
```gamebatch advinit``` is not supported, yet.

## Commands that don't use the server
There are a couple of commands that do not use the server.
This of course, means that they are faster that commands that do use the server.
```batch
:: The following commands sets the print color
:: The difference between this and the native color command is that this one only affects text printed after calling this method.
gamebatch fg a
gamebatch bg b

:: Printing without any new-lines
:: Why not just use echo|set /p="Hello World" ? It is slow and very dangerous.
gamebatch print "Hello there, "
echo %NAME%

:: This one sets the print position
gamebatch setpos %X% %Y%

:: This one outputs the terminal width to %TEMP%\GAMEBATCH
gamebatch termw
set /p theResult=%TEMP%\GAMEBATCH

:: There is also this
gamebatch termh
```

### Commands in the simple server build
```batch
gamebatch simpleinit

:: Setting variables
gamebatch setvar %NameOfVariable% 5 + 5

:: Arithmetic operations
:: Use this only if your arithmetic operations use floating-point numbers
:: Else use simple `set` stuff.
gamebatch math %NameOfVariable% ^ 2
set /p theResult=%TEMP%\GAMEBATCH

:: Getting variables
gamebatch variable %NameOfVariable%
set /p theResult=%TEMP%\GAMEBATCH

:: Run multiple commands at once
::To save time (because of server commands being slow), you can send multiple commands to be ran one by one!
gamebatch setvar a 5;math a + 5
set /p theResult=%TEMP%\GAMEBATCH
:: In old versions there may not be a space after the semicolon.

:: Temporary variables
:: The above code can be more organized if using temporary variables that disappear after running multiple commands.
gamebatch a=5;math a + 5
set /p theResult=%TEMP%\GAMEBATCH

:: Use this at the end of your game
gamebatch exit
```

### Exiting GameBatch
GameBatch uses a temporary file in the `%TEMP%` folder, called `GAMEBATCH`.  
If you are using a server, you can just do `gamebatch exit`.  
However, if you are not using a server, you must then delete the file manually by using `del %TEMP%\GAMEBATCH`.  
The `GAMEBATCH` file doesn't get bigger than a kilobyte, though.
