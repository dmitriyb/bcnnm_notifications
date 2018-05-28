package net.bcnnm.notifications.calcs;

import javax.xml.bind.SchemaOutputResolver;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatcherTest {
    public static void main(String[] args) {

        String params = "Min key prefix";

        Matcher matcher = Pattern.compile("\\(?([a-zA-Z,\\s]*)\\)? (.*) (.*)").matcher(params);

        matcher.matches();
        String[] functions = matcher.group(1).split(",\\s*");
        String key = matcher.group(2);
        String prefix = matcher.group(3);

        Pattern pattern = Pattern.compile("[a-zA-Z]+");
        Matcher m = pattern.matcher("(std, Max, Min) key prefix");

        while (m.find()) {
            System.out.println(m.group());
        }
    }
}
