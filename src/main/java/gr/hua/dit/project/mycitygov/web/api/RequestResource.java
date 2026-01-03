package gr.hua.dit.project.mycitygov.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import gr.hua.dit.project.mycitygov.core.service.RequestService;
import gr.hua.dit.project.mycitygov.core.service.model.TicketDetailsView;
import gr.hua.dit.project.mycitygov.core.service.model.TicketListItem;
import gr.hua.dit.project.mycitygov.web.ui.model.OpenTicketForm;
import gr.hua.dit.project.mycitygov.web.ui.model.UpdateTicketStatusForm;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/requests", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Requests", description = "Operations for citizen/employee requests")
public class RequestResource {

   private final RequestService requestService;

   public RequestResource(final RequestService requestService) {
      if (requestService == null)
         throw new NullPointerException();
      this.requestService = requestService;
   }

   @Operation(summary = "List requests for the current user")
   @GetMapping("")
   public List<TicketListItem> list() {
      return this.requestService.getTicketsForCurrentUser();
   }

   @Operation(summary = "Get request details for the current user")
   @GetMapping("/{id}")
   public TicketDetailsView details(@PathVariable("id") final Long id) {
      return this.requestService.getTicketForCurrentUser(id);
   }

   @Operation(summary = "Create a new request (citizen)")
   @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
   public TicketDetailsView create(@Valid @RequestBody final OpenTicketForm form) {
      return this.requestService.createCitizenTicket(form);
   }

   @Operation(
         summary = "Assign request to current employee",
         responses = {
               @ApiResponse(responseCode = "200", description = "Assigned",
                     content = @Content(schema = @Schema(implementation = TicketDetailsView.class))),
               @ApiResponse(responseCode = "403", description = "Not allowed")
         })
   @PostMapping("/{id}/assign")
   public TicketDetailsView assign(@PathVariable("id") final Long id) {
      return this.requestService.assignToCurrentEmployee(id);
   }

   @Operation(summary = "Update status (employee)")
   @PutMapping(value = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE)
   public TicketDetailsView updateStatus(
         @PathVariable("id") final Long id,
         @Valid @RequestBody final UpdateTicketStatusForm form) {
      return this.requestService.updateStatus(id, form);
   }
}
