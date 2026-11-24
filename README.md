# Vaadin 25.4 Feature Tour

*A Vaadin DevRel demo app.*

A hands-on tour of [Vaadin 25.4](https://github.com/vaadin/platform/releases/tag/25.4.0-beta2): every page is a live
demo of one release feature, with the Java code behind it and a link to the documentation. The successor of the
[Vaadin 25.3 Feature Tour](https://github.com/SebastianKuehnau/vaadin-25-3-feature-tour).

Built with Vaadin Flow 25.4.0-beta2, Spring Boot 4.2 and the Aura theme.

## What's inside

| Area | Page | Feature | License |
|---|---|---|---|
| Security | Strict CSP | `vaadin.csp` = `off` / `warn` / `strict`, a legacy string `executeJs` next to its declared replacement, CSP warnings captured live | Free |
| | Declared JavaScript | `@JsDefinition` + `@JsExpression`, typed `JavaScriptResult<T>` (clipboard, a record), `DomEventFilter` + `addEventDataPath()`, old vs. new | Free |
| Flow | Media & Native Form | `Video`, `Audio`, `Source` from range-enabled `DownloadHandler`s with a live log of byte ranges, `NativeForm` login | Free |
| | Downloads Everywhere | `Download.onClick()` on a Card, `DownloadTarget.NEW_TAB`, `Button.setDownloadHandler()`, Menu Bar and Context Menu items | Free |
| | Hierarchical Menus | `@Menu(parent = ...)` and `MenuConfiguration.getMenuEntriesTree()` build this app's own side nav | Free |
| | Lifecycle Events | Request and navigation start/end with handler, outcome and duration; `vaadin-request` / `vaadin-server-message` round trips in the browser | Free |
| | Styling | `@StyleSheet(layer = "...")` losing to app styles on purpose | Free |
| | Runtime ES Modules | `@JavaScript(type = MODULE)` loads a plain ES module at runtime without Vite, `Page.addJavaScript(url, loadMode, type)` | Free |
| | Small API Wins | `withNullSafeConverter`, `Component.findAncestor(predicate)`, `ExtendedClientDetails.getZoneId()`, `RouteConfiguration.getUrl()` with query parameters | Free |
| Components | PDF Viewer | From a URL and a `DownloadHandler`, page navigation and zoom from Java, page signals, custom toolbar button | Commercial, preview |
| | Forms: Labels Aside | `LABEL_ASIDE` on eight field types, `FormLayout.setLabelTextAlign()` | Free |
| | Multi-Select Combo Box | Select All / Select Filtered, hidden with lazy data | Free |
| | Text Selection | `setSelectionRange()` on the invalid part after validation, `selectAll()`, `setCursorPosition()` | Free |
| | Button | `DisableOnClickMode.UNTIL_RESPONSE` vs. no protection vs. `setDisableOnClick(true)`, with a 2 s listener | Free |
| | Grid | Multi-select by row click, `getColumnBySortProperty()`, context menu that knows the clicked column | Free |
| | Now Stable | Switch and Breadcrumbs without feature flags | Free |
| | Accessibility & More | Heading levels (Dialog, Confirm Dialog, Card), Dialog `aria-describedby`, camera capture, `findTabContaining()`, `Notification.getText()`, Slider variants | Free |
| AI | AI Grid Paging | `GridAIController` paging through `DatabaseProvider.executeQuery(sql, QueryPage)` and `count(sql)`, `submitDisabled` | Commercial, preview |
| Tooling | Copilot 25.4 | Guided page: Browserless tests from the recorder, Kotlin views, auto-responsive Form Layout, Component Gallery, drilldown, optimistic updates | – |
| | Under the Hood | Hilla TypeScript in Java, leaner `vaadin-spring`, Node.js 26, Spring Boot 4.2, CDI/Quarkus versioning, breaking changes | – |

## Running the app

Requirements: Java 21 or later. Maven and Node.js are handled by the wrapper and the Vaadin plugin (it downloads
Node.js 26 into `~/.vaadin` if needed). 25.4 is a pre-release: the `vaadin-prereleases` repository is configured in
`pom.xml`.

```bash
./mvnw
```

Then open http://localhost:8888. Set `PORT` to use a different port.

The app runs with `vaadin.csp=warn`. Switch the mode with `VAADIN_CSP`:

```bash
VAADIN_CSP=strict ./mvnw
```

Commercial features (PDF Viewer, AI Grid) ask for a Vaadin license in the browser the first time you use them. A
[free trial](https://vaadin.com/trial) works. The PDF Viewer and the AI features are previews and are enabled in
`src/main/resources/vaadin-featureflags.properties`.

### AI demos

The AI Grid page talks to an LLM through Spring AI. OpenAI is the default:

```bash
export OPENAI_API_KEY=sk-...
./mvnw
```

To use Anthropic instead:

```bash
export AI_PROVIDER=anthropic
export ANTHROPIC_API_KEY=sk-ant-...
./mvnw
```

The models are set in `application.properties` (`gpt-5.4-mini`, `claude-sonnet-5-5`). Without a key, the AI page
shows a hint instead of the live demo. The grid runs the SQL written by the LLM as a read-only H2 user. The LLM sees
the schema, never the rows. Ask for "all orders" and scroll: the paging log shows each page the grid fetches.

## Tests

```bash
./mvnw test
```

Browserless tests using the new 25.4 testing APIs:

- `withTestId()` / `testId()` for every lookup (the demo components have test IDs)
- `reload()` in the Hierarchical Menus and Lifecycle Events tests
- `roundTrip()` to run the Lifecycle dashboard's `UI.access()` refresh
- `UploadButton`, `UploadDropZone` and `UploadFileList` testers with successful, failed, aborted and rejected uploads
- Dedicated tests for Text Selection (it checks the declared `setSelectionRange` call), Button `UNTIL_RESPONSE` and
  Labels Aside
- The `DatabaseProvider` paging defaults behind the AI Grid, against H2 without an LLM
- A smoke test that opens every page

## Project structure

```
src/main/java/com/vaadin/devrel/featuretour
├── base/ui              MainLayout (side nav from the menu tree + breadcrumbs), ShowcasePage, AreaPage,
│                        ShowcaseInfo, Tier badges
└── showcase
    ├── home             Overview page
    ├── security         Strict CSP, declared JavaScript
    ├── flow             Media & native form, downloads, menus, lifecycle events, styling, small API wins
    ├── components       PDF Viewer, labels aside, multi-select combo box, text selection, button, grid, ...
    ├── ai               AI Grid paging, LLM and database setup
    └── tooling          Copilot 25.4, under the hood
src/main/resources
├── META-INF/resources
│   ├── styles.css             App styles
│   ├── vaadin-blue-inter.css  Aura theme configuration (Vaadin blue accent, Inter)
│   ├── styles/                The cascade layer demo style sheet
│   ├── js/                    The runtime ES module demo
│   └── pdf/                   Sample PDF (a tagged 4-page cheat sheet)
└── media/                     Generated test video and audio for the media demo
```

Every page declares its area with `@Menu(parent = ...)` and its card text and badges with `@ShowcaseInfo`. The side
nav, the Home page and the area pages are all built from `MenuConfiguration.getMenuEntriesTree()`.

## Known gaps in beta2

What didn't work as announced, or couldn't be verified while building this tour:

- **`OpenInNewTab.onClick()`** from the release notes is not public API in beta2 (only an internal
  `OpenInNewTabAction`). The Downloads page uses `Download.onClick(component, handler, DownloadTarget.NEW_TAB)`, which
  does the same, and says so on the page.
- **Strict CSP is not production-only.** `vaadin.csp=strict` makes string JavaScript throw `CspViolationException`
  in development mode too; only the nonce-based `Content-Security-Policy` header is limited to production. Vaadin's
  own event filters for double/single click listeners and keyboard shortcuts still use `eval` and throw in strict
  mode (documented in `CspMode`).
- **`NavigationEndedEvent` has no duration.** The Lifecycle page pairs it with `NavigationStartedEvent` per UI.
  `RequestStartedEvent` has no session, and the first request of a page load starts before the session exists.
- **`RouteConfiguration.getUrl()` with query parameters** only exists as `getUrl(target, RouteParameters,
  QueryParameters)`; pass `RouteParameters.empty()`.
- **Browserless**: `reload()` reports the new navigation with the trigger `PROGRAMMATIC`, not `REFRESH`.
  `ButtonTester.click()` runs the before-response callbacks itself, so an `UNTIL_RESPONSE` button is enabled again when
  `click()` returns (the test checks the disabled state from a click listener). There is no tester API for text
  selection; the test inspects the pending `JsCall` instead.
- **Not verified in the environment this tour was built in:**
  - PDF Viewer rendering and a production build (`./mvnw package`): the available license key does not cover
    `vaadin-pdf-viewer` 25.4.0-beta1, so the viewer showed the license notice and the production build stopped at the
    license check. Consequently, the production CSP header was not checked either.
  - AI Grid paging with a live LLM (no API key). The paging defaults are covered by `PagingDatabaseProviderTest`.
  - The Copilot features (guided page only).
  - Docs links: `vaadin.com` was not reachable. Links that appear in the beta2 release notes are used as is; the others
    (`flow/routing/menu`, `components/pdf-viewer`, `components/grid`, `components/dialog`, `components/switch`,
    `flow/ai-support/ai-powered-grid`, `flow/advanced/session-lock-and-rpc-listeners`,
    `flow/binding-data/components-binder-validation`) follow the 25.3 tour's paths under `docs/next` and may move.

## Links

- [Release notes 25.4.0-beta2](https://github.com/vaadin/platform/releases/tag/25.4.0-beta2)
- [Release notes 25.4.0-beta1](https://github.com/vaadin/platform/releases/tag/25.4.0-beta1)
- [Vaadin documentation (next)](https://vaadin.com/docs/next)
- [Vaadin 25.3 Feature Tour](https://github.com/SebastianKuehnau/vaadin-25-3-feature-tour)
