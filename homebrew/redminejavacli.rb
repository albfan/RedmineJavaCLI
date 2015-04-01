class Redminejavacli < Formula
  homepage "http://a11n.github.io/RedmineJavaCLI"
  url "https://github.com/a11n/RedmineJavaCLI/releases/download/v1.1.0/RedmineJavaCli-1.1.0.jar"
  version "1.1.0"
  sha256 "a8d06148b5ecdfe15b8a756a3b7147f667fabc31caf88211858c1c8d3e76ced3"

  depends_on :java

  def install
    jar = "RedmineJavaCli-1.1.0.jar"
    java = share/"java"
    java.install jar
    bin.write_jar_script java/jar, "redmine"
  end

  test do
    system "#{bin}/redmine"
  end
end
