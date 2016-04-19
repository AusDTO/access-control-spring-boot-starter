package au.gov.dto.security.web;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Controller;

@Controller
public class BaseController {

    protected static final Marker AUDIT = MarkerFactory.getMarker("AUDIT");

}
