package de.ad.tools.redmine.cli.command;

import java.util.Map;

public interface IHandler {
    String getName();

    void handle(Map<String, String> parameters, String value) throws Exception;
}
