---
layout: page
title: Installation
---

##3-Step-Setup
* Download [RedmineJavaCLI]({{ site.github.repo }}/releases/download/v{{ site.version }}/RedmineJavaCli-{{ site.version }}.jar)
* Create an alias
{% highlight bash %}
alias redmine="java -jar ./RedmineJavaCli-{{ site.version }}.jar $@"
{% endhighlight %}
* Connect to your Redmine instance
{% highlight bash %}
>redmine connect http://your.server/redmine apiKey
{% endhighlight %}
You are all set. Have fun :-)

##OS X - Homebrew
If you are running OS X along with Homebrew installation is even simpler.
{% highlight bash %}
brew install https://raw.github.com/albfan/RedmineJavaCLI/master/homebrew/redminejavacli.rb
{% endhighlight %}
This will download RedmineJavaCli and setup an according alias for you.

Last but not least connect to your Redmine instance like demonstrated above.
