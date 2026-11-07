## 1. Remove redundant imports

- [x] 1.1 Remove the same-package `com.example.template.items.*` imports from `CreateItemController`, `ListItemsController`, `ItemService`, and `ItemDtoMapper`
- [x] 1.2 Remove the same-package `com.example.template.items.*` imports from `CreateItemControllerTests`, `ListItemsControllerTests`, and `ItemServiceTests`
- [x] 1.3 Confirm no import of a class from the `com.example.template.items` package remains in files that live in that package (`grep -rn "^import com.example.template.items\." src`)

## 2. Verification

- [x] 2.1 Run `./mvnw verify` and confirm the same 39 tests still pass; report which checks executed
