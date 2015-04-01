---
layout: page
title: User guide
---

##Available commands
To get a list of available command just call `redmine help`.
{% highlight bash %}
>redmine help
help          Display general help or (if provided) command help.  
connect       Connect to server using API key for authentication.  
projects      Display your projects.
project       Display project details.
issues        Display issues.
issue         Display issue details.
history       Display issue history.
list          List the specified entity.
create-issue  Create a new issue.
update-issue  Update a given issue.
open          Open issue in default browser.
reset         Reset the current configuration.
{% endhighlight %}

The `help` command also features a dedicated help for each command.
{% highlight bash %}
>redmine help create-issue
COMMAND
¯¯¯¯¯¯¯
create-issue

DESCRIPTION
¯¯¯¯¯¯¯¯¯¯¯
Create a new issue.

usage: redmine create-issue <projectKey> <subject> --option=value

<projectKey>  The key of the project to add this issue to.  
<subject>     The subject of the issue.

--description  The description of the issue to create.  
--priority     The priority of the issue to create.
--assignee     The assignee of the issue to create.
--status       The status of the issue to create.
--tracker      The tracker of the issue to create.
{% endhighlight %}

##Example
{% highlight bash %}
>redmine issues --priority=High --status="In Progress"
ID    TRACKER  STATUS       PRIORITY  ASSIGNEE        UPDATED     SUBJECT
¯¯    ¯¯¯¯¯¯¯  ¯¯¯¯¯¯       ¯¯¯¯¯¯¯¯  ¯¯¯¯¯¯¯¯        ¯¯¯¯¯¯¯     ¯¯¯¯¯¯¯
#127  Feature  In Progress  High      (not assigned)  3 days ago  New subject
#126  Feature  In Progress  High      John Doe        6 days ago  This is a new issue.  
{% endhighlight %}
**Note:** Have you noticed the quotation marks encapsulating `"In Progress"`? Use these to escape values with whitespaces. Feel free to skip them for simple values, such as `High`.

##Protips
Although the `help` command is pretty straightforward, here are some pro tips for you.

###Open issue in default browser.
This tool is intended as an enhancement rather than a replacement of the Redmine web solution. So there are good reasons to use both in parallel. To make this as convenient as possible for you, there is the `open` command, which opens a specified issue in your default browser for you.
{% highlight bash %}
>redmine open 1
Opened issue #1 in default browser.
{% endhighlight %}

###Which statuses, priorities and trackers are configured?
Redmine features the ability to configure certain properties of an issue. So there is no general assignment available.  
This means, for example `--priority=High` might not be available on every Redmine instance. In order to filter, create or update you need to know the available assignments.  
That's where the `list` command comes into the game.
{% highlight bash %}
>redmine list status
NAME         ID  DEFAULT  CLOSED  
¯¯¯¯         ¯¯  ¯¯¯¯¯¯¯  ¯¯¯¯¯¯  
New          1   X
In Progress  2
Resolved     3
Feedback     4
Closed       5            X
Rejected     6            X
{% endhighlight %}


###How to obtain my API key?
In order to use this tool you are required to provide an **API key** for authentication.

1. Login into Redmine
2. Click "My account" (in the upper right corner)
3. On the right-hand-side panel you could find your API key within the *API access key* section
4. Copy this key.

**Note:** If you **do not** see this section, most probably the REST API access of your Redmine instance has been disabled. If you are the administrator of the server you could enable it in the server settings.
