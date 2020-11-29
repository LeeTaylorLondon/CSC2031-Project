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

Users may input an existing username and password to login. Note failing to login after 3 attempts 
will disable the login button.

![Image of Login](/images/login.png)  

Users may create an account. Note the phone number should be 13 digits with dashes in place shown in 
the image below. Also, note a password must contain more than 8 characters but less than 15, with at least
one upper and lower case letter and a digit. A user may register as an admin granting them access to the 
admin page.

![Image of Register](/images/register.png)

For a non-admin user they may choose 6 lottery numbers. Either by inputting 6 numbers or clicking the
lucky dip button that adds 6 secure random numbers for them. Note the user must click 'add users numbers'
to actually store the numbers. The get draws button displays all the numbers chosen by the user in a table.
The check your pickings button checks if the current user's numbers match the winning numbers.

![Image of Logged in User Account](/images/account.png)

An admin may view all the user account information, except the password hash. They may also see the winning 
numbers and previous results. Lastly they may spin a new set of winning numbers.

![Image of admin page](/images/admin.png)

### 