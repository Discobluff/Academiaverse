# Setup Instructions

## Frontend ([Angular](https://angular.dev/))

### Installation

- [Install Angular CLI (requires Node.js)](https://angular.dev/tools/cli/setup-local)
- ```npm install zone.js```

### Execution

Go to `src/frontend`

```
ng serve
ng serve --configuration=ar --port 4201
```

### Components

The components are located in directories in `[src/frontend]/src/app`.

#### Generate a new component

```
ng generate component component-name
```

#### Component structure

```src/app/
├── component-name/
│   ├── component-name.html
│   ├── component-name.css
│   ├── component-name.ts
│   ├── component-name.spec.ts
```

#### Add a new route

When you create a new component, you can add it to the routing module to make it accessible via a URL.
Example : to render `component-name` at the path `/component-name`, add the following to `app.routes.ts`:

```typescript
{
    path: 'component-name',
    loadComponent: () => import('./component-name/component-name').then(m => m.ComponentName)
},
```

### Tests

[End-to-end tests](https://angular.dev/tools/cli/end-to-end)