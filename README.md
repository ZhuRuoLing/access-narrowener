# access-narrowener

> [!IMPORTANT]  
> Do NOT use this project in production, this project just is a toy

A java library to "narrowen" the access of fields in a class.  

### How does it "narrowen" field accesses
It changes all non-final and non-private field into a private field and generates a getter and setter.
Then it will transform all field accesses into method calls.  

### How to use

```java
import icu.takeneko.accessnarrowener.AccessNarrowener;
import icu.takeneko.accessnarrowener.TransformRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Example {
    public static void main(String[] args) {
        // classes you want to transform
        List<String> classes = List.of();
        // Get all classes matching the regex pattern
        String pattern = "com.example";
        List<String> scannedClasses = AccessNarrowener.findAllClassMatching(pattern);
        // Call the Narrowener
        Map<String, byte[]> result = AccessNarrowener.process(classes, TransformRule.DEFAULT);
        // The key of result map is class name
        // The value of result map is class bytecode
    }
}
```

