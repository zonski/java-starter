package au.com.roaringventures.api.portal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/portal/test")
public class TestPortalApi {

    @GetMapping()
    public Map<String, Object> test() {
        Map<String, Object> map = new HashMap<>();
        map.put("test", "Portal request");
        return map;
    }

}
