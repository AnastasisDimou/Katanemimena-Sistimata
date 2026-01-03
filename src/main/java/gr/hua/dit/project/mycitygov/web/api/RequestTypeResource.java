package gr.hua.dit.project.mycitygov.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import gr.hua.dit.project.mycitygov.core.model.RequestType;
import gr.hua.dit.project.mycitygov.core.repository.RequestTypeRepository;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/request-types", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Request Types")
public class RequestTypeResource {

   private final RequestTypeRepository requestTypeRepository;

   public RequestTypeResource(final RequestTypeRepository requestTypeRepository) {
      if (requestTypeRepository == null)
         throw new NullPointerException();
      this.requestTypeRepository = requestTypeRepository;
   }

   @Operation(summary = "List active request types")
   @GetMapping("")
   public List<RequestType> listActive() {
      return this.requestTypeRepository.findByActiveTrueOrderByNameAsc();
   }
}
