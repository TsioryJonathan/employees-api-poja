    package com.poja.employees.endpoint.rest.controller.administration;

    import com.poja.employees.endpoint.rest.dto.EmployeeRequest;
    import com.poja.employees.model.dto.EmployeeResponse;
    import com.poja.employees.model.dto.IndividualResponseWrapper;
    import com.poja.employees.model.dto.ResponseWrapper;
    import com.poja.employees.service.EmployeeService;
    import lombok.AllArgsConstructor;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.domain.Sort;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/api/employees")
    @AllArgsConstructor
    public class EmployeeController {

        private final EmployeeService employeeService;

        @GetMapping
        public ResponseEntity<ResponseWrapper<EmployeeResponse>> getAllEmployees(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size,
                @RequestParam(required = false) String sort,
                @RequestParam(required = false) String order
        ) {
            Pageable pageable = buildPageable(page, size, sort, order);
            ResponseWrapper<EmployeeResponse> response = employeeService.getAllEmployees(pageable);
            return ResponseEntity.ok(response);
        }

        @GetMapping("/{id}")
        public ResponseEntity<IndividualResponseWrapper<EmployeeResponse>> getEmployeeById(@PathVariable long id) {
            return ResponseEntity.status(HttpStatus.OK).body(employeeService.getEmployeeById(id));
        }

        /* Creation of an employee */
        @PostMapping
        public ResponseEntity<IndividualResponseWrapper<EmployeeResponse>> createEmployee(@RequestBody EmployeeRequest employeeRequest) {
            return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createEmployee(employeeRequest));
        }
        /* Utils */
        private Pageable buildPageable(int page, int size, String sort, String order) {
            if (sort != null && !sort.isEmpty()) {
                Sort.Direction direction = order != null && order.equalsIgnoreCase("DESC")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;
                return PageRequest.of(page, size, Sort.by(direction, sort));
            }
            return PageRequest.of(page, size);
        }
    }