package capital.scalable.restdocs.example.a;


import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SomeController {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.initDirectFieldAccess();
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /**
     * Some description
     *
     * @param someClass   This is some header
     */
    @GetMapping("/some")
    void some(@RequestHeader("someClass") SomeClass someClass,
              SomeRequest someRequest) {

    }
}
