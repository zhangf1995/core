package zf.core.annoValue;

import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

public class CustomPropertySource extends PropertiesPropertySource {
    public CustomPropertySource(String name, Properties source) {
        super(name, source);
    }

}
