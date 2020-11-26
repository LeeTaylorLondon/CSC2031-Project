# CSC2031 Project

### Synopsis

This program provides a fully functioning lottery website for users to register and login to. 
Regular users (non-admin) may add any number of sets, each consisting of 6 numbers between 0 and 60. Users may also
check if the numbers they've chosen match the winning numbers. 

### Correctness Testing

See [report](/Report.pdf) 'testing' section for a detailed explanation on strategies for testing components. Also run
the [Tests class](/src/Tests.java) which features two different tests. Demonstrating a public and 
private key can be created, stored in a text file and extracted in object form. Read [Tests class](/src/Tests.java)
doc-strings and comments for more details.

### How To Use - Entry Point

Windows users, open command line (CMD) from the search bar. Then navigate from C:\Users\username to
the directory where the file 'docker-compose.yml' is located. Which is contained in the directory
'CSC2031 Coursework'. This can be done by using the 'cd' command. 

Mac users, may navigate to the correct directory 'CSC2031 Coursework' which should contain 'docker-compose.yml'
and then press the keyboard shortcut opening the command line. 

Once in the correct directory then type and execute the command 'docker-compose up'. Typing 
http://localhost:44444/ into your browser should open the lottery website. To gracefully close
the application press CTRL+C and type 'docker-compose down'.  

Users may input a username and password to login.

![Image of Login](/images/login.png)  

Users may create an account. Note the phone number should be 13 digits with dashes in place shown in 
the image below. Also note a password must contain more than 8 characters but less than 15, with atleast
one upper and lower case letter and a digit. 

![Image of Register](/images/register.png)

### 