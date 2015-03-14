# RedmineJavaCLI [![Build Status](https://travis-ci.org/a11n/RedmineJavaCLI.svg)](https://travis-ci.org/a11n/RedmineJavaCLI) [![Coverage Status](https://coveralls.io/repos/a11n/RedmineJavaCLI/badge.svg)](https://coveralls.io/r/a11n/RedmineJavaCLI)

A Redmine CLI written in Java.

##Usage

For convenience I recommend to create an alias first:
```shell
alias redmine="java -jar ./build/libs/RedmineJavaCli-1.0.jar $@"
```

Let's display a list of available commands:
```shell
>redmine help
usage: redmine <command> [<args>]

help      [<command>]      Display general help or (if provided) command help.  
connect   <url> <apiKey>   Connect to server using API key for authentication.  
projects                   Displays your projects.                              
project   <key>            Display project details.                             
issues                     Displays issues.                                     
issue     <id>             Display issue details.                               
history   <id>             Display issue history.                               
open      <id>             Open issue in default browser.                       
reset                      Reset the current configuration. 
```

**Note:** There is also a per-command help available, just type `redmine help connect` for example.

Before querying a Redmine instance we need to connect to a server beforehand:
```shell
>redmine connect http://localhost:8080/redmine f92db342be05601b7ce84e98a829bd5d6a65db21
Successfully connected user 'admin' to server 'http://localhost:8080/redmine'.
```

**Note:** Currently only API key authorization is supported. But in a next version there will be also "noauth" and user/password authorization support.

Now list your projects or have a look at your project details:

```shell
>redmine projects
Name            Key
¯¯¯¯            ¯¯¯
Test Project 1  test-project-1  
Test Project 2  test-project-2

>redmine project test-project-1
Test Project 1

This is a test project.

Members
¯¯¯¯¯¯¯
Manager:    Admin Istrator  
Developer:  John Doe
```

Use the same approach to investigate your issues:
```shell
>redmine issues
ID  Tracker  Status       Priority  Assignee        Updated        Description         
¯¯  ¯¯¯¯¯¯¯  ¯¯¯¯¯¯       ¯¯¯¯¯¯¯¯  ¯¯¯¯¯¯¯¯        ¯¯¯¯¯¯¯        ¯¯¯¯¯¯¯¯¯¯¯         
#2  Feature  In Progress  High      (not assigned)  5 minutes ago  This is a feature.  
#1  Bug      New          Normal    Admin Istrator  5 minutes ago  This is a bug. 

>redmine issue 1
Bug #1
This is a bug.
Added by Admin Istrator 6 minutes ago. 

Status  Priority  Assignee              
¯¯¯¯¯¯  ¯¯¯¯¯¯¯¯  ¯¯¯¯¯¯¯¯              
New     Normal    Admin Istrator  

Description
¯¯¯¯¯¯¯¯¯¯¯
This is a bug description.
```

Unfortunately the current support of this CLI is focused on just displaying information. There will be commands to update your projects or issues in a next version. For now, just open them in your browser for manipulation:

```shell
>redmine open 1
```
