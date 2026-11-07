## 1. Service

- [x] 1.1 Replace `items/ItemNotFoundException` with a generic `ResourceNotFoundException(resource, id)` (unchecked, no HTTP dependency) in the root package
- [x] 1.2 Make `ItemService.get(Long id)` (`@Transactional(readOnly = true)`) throw `ResourceNotFoundException` when absent
- [x] 1.3 Update `ItemServiceTests` (mocked repository): returns entity when found, throws `ResourceNotFoundException` when absent

## 2. HTTP

- [x] 2.1 Add `GetItemController` for `GET /api/v1/items/{id}` returning `ItemDto`
- [x] 2.2 Change the `ApiExceptionHandler` handler to `@ExceptionHandler(ResourceNotFoundException.class)` returning a 404 `ProblemDetail` whose detail is "<resource> not found"
- [x] 2.3 Update `GetItemControllerTests` (`@WebMvcTest`, mocked service): 200 with JSON body, 404 problem response, 400 for non-numeric ID with no service call

## 3. Verification and documentation

- [x] 3.1 Re-run `./mvnw verify` and report which checks executed
- [x] 3.2 Re-verify `GET /api/v1/items/{id}` against local PostgreSQL (existing ID, missing ID, non-numeric ID) and record results
- [x] 3.3 Update README and AGENTS.md (lookup by ID no longer pending) once 3.1 and 3.2 pass
