# RedmineJavaCLI [![Build Status](https://travis-ci.org/a11n/RedmineJavaCLI.svg)](https://travis-ci.org/a11n/RedmineJavaCLI) [![Coverage Status](https://coveralls.io/repos/a11n/RedmineJavaCLI/badge.svg)](https://coveralls.io/r/a11n/RedmineJavaCLI)

You already have a **powerful, console-based** workflow in place?
*git, svn, gradle, mvn* - you are mastering them all. And you are fast!
But Redmine breaks your workflow?

**Not anymore:** RedmineJavaCLI is here for you.
A convenient, comprehensive, stateful Redmine client.
Your new companion.

##3-Step-Setup
* Download RedmineJavaCLI
* Create an alias 
```shell
alias redmine="java -jar ./RedmineJavaCli-1.0.jar $@"
```
* Connect to your Redmine instance
```shell
>redmine connect http://localhost:8080/redmine f92db342be05601b7ce84e98a829bd5d6a65db21
Successfully connected user 'admin' to server 'http://localhost:8080/redmine'.
```
You are all set. Have fun :-)

##Usage
Just type `redmine help` to see what's waiting for you:
```shell
> redmine help
usage: redmine <command> [<args>] [<opts>]

help          [<command>]              Display general help or (if provided) command help.  
connect       <url> <apiKey>           Connect to server using API key for authentication.  
projects                               Display your projects.                               
project       <key>                    Display project details.                             
issues                                 Display issues.                                      
issue         <id>                     Display issue details.                               
history       <id>                     Display issue history.                               
list          <entity>                 List the specified entity.                           
create-issue  <projectKey> <subject>   Create a new issue.                                  
update-issue  <id>                     Update a given issue.                                
open          <id>                     Open issue in default browser.                       
reset                                  Reset the current configuration.
```
