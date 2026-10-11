package ch.bbw.pr.tresorbackend.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;

@Slf4j
@Service
public class ErrorHandler {
    public ResponseEntity<String> handleErrors(BindingResult bindingResult, String location) {
        List<String> errors = bindingResult.getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();
        log.info("{} {}", location, errors);

        JsonArray arr = new JsonArray();
        errors.forEach(arr::add);
        JsonObject obj = new JsonObject();
        obj.add("message", arr);
        String json = new Gson().toJson(obj);

        log.info("{}, validation fails: {}", location, json);
        return ResponseEntity.badRequest().body(json);
    }
}
