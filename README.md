# RedmineJavaCLI [![Build Status](https://travis-ci.org/a11n/RedmineJavaCLI.svg)](https://travis-ci.org/a11n/RedmineJavaCLI) [![Coverage Status](https://coveralls.io/repos/a11n/RedmineJavaCLI/badge.svg)](https://coveralls.io/r/a11n/RedmineJavaCLI)

You already have a **powerful, console-based** workflow in place?  
*git, svn, gradle, mvn* - you are mastering them all. And you are fast!  
But Redmine breaks your workflow?

**Not anymore:** RedmineJavaCLI is here for you.  
A convenient, comprehensive, stateful Redmine client.  
Your new companion.

For setup instructions, user guide and protips see the [project page](http://a11n.github.io/RedmineJavaCLI).

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
##Example
This is how it looks like in action.
```shell
>redmine issues --priority=High --status="In Progress"
ID    TRACKER  STATUS       PRIORITY  ASSIGNEE        UPDATED     SUBJECT
¯¯    ¯¯¯¯¯¯¯  ¯¯¯¯¯¯       ¯¯¯¯¯¯¯¯  ¯¯¯¯¯¯¯¯        ¯¯¯¯¯¯¯     ¯¯¯¯¯¯¯
#127  Feature  In Progress  High      (not assigned)  3 days ago  New subject
#126  Feature  In Progress  High      John Doe        6 days ago  This is a new issue.  
```
