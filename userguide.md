---
layout: page
title: User guide
---

For convenience I recommend to create an alias first:
{% highlight bash %}
alias redmine="java -jar /<pathToJar>/RedmineJavaCli-1.0.jar $@"
{% endhighlight %}

Let's display a list of available commands:
{% highlight bash %}
>redmine help
usage: redmine <command> [<args>]

help          [<command>]       Display general help or (if provided) command help.  
connect       <url> <apiKey>    Connect to server using API key for authentication.  
projects                        Display your projects.
project       <key>             Display project details.
issues                          Display issues.
issue         <id>              Display issue details.
history       <id>              Display issue history.
list          <entity>          List the specified entity.
update-issue  <id> <keyValue>   Update a given issue.
open          <id>              Open issue in default browser.
reset                           Reset the current configuration.
{% endhighlight %}

**Note:** There is also a per-command help available, just type `redmine help connect` for example.

Before querying a Redmine instance we need to connect to a server beforehand:
{% highlight bash %}
>redmine connect http://localhost:8080/redmine f92db342be05601b7ce84e98a829bd5d6a65db21
Successfully connected user 'admin' to server 'http://localhost:8080/redmine'.
{% endhighlight %}

**Note:** Currently only API key authorization is supported. But in a next version there will be also "noauth" and user/password authorization support.

Now list your projects or have a look at your project details:

{% highlight bash %}
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
{% endhighlight %}

Use the same approach to investigate your issues:
{% highlight bash %}
>redmine issues
ID  Tracker  Status       Priority  Assignee        Updated        Subject
¯¯  ¯¯¯¯¯¯¯  ¯¯¯¯¯¯       ¯¯¯¯¯¯¯¯  ¯¯¯¯¯¯¯¯        ¯¯¯¯¯¯¯        ¯¯¯¯¯¯¯
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
{% endhighlight %}

Now if you'd like to update an issue, feel free to do so by using the `update-issue` command:
{% highlight bash %}
>redmine update-issue 1 status="In Progress"
Sucessfully updated issue #1.
{% endhighlight %}
Currently the following issue properties could be updated:
- description, subject, priority, assignee, status, tracker

**Remark**: For single word value you could use a simple assignment `key=value`. If the value consist of multiple words, just surround it by quotes to make sure it's handled properly, e.g. `status="In Progress"`.

Since some of those properties (priority, status and tracker) are fully customizable on your Redmine server, you can list their available values with another command:
{% highlight bash %}
>redmine list status
Name         ID  Default  Closed  
¯¯¯¯         ¯¯  ¯¯¯¯¯¯¯  ¯¯¯¯¯¯  
New          1   X
In Progress  2
Resolved     3
Feedback     4
Closed       5            X
Rejected     6            X
{% endhighlight %}

You could also list the values for priority and tracker.

Last but not least, if you still fill the need to use your browser, just continue working in your browser with this simple command:
{% highlight bash %}
>redmine open 1
Opened issue '1' in default browser.
{% endhighlight %}
