import dev.fritz2.core.render
import dev.fritz2.core.storeOf
import dev.fritz2.headless.components.combobox
import dev.fritz2.headless.foundation.portalRoot
import kotlinx.browser.document
import kotlinx.coroutines.flow.map
import org.w3c.dom.HTMLElement


fun main() {
    val mainDiv = document.getElementById("main") as? HTMLElement

    render(mainDiv!!) {
        val itemsStore = storeOf(listOf("one", "two", "three"))
        val selectionStore = storeOf(emptyList<String>())
        val selectedStore = storeOf<String?>(null)

        selectedStore.data.handledBy { selected ->
            if (selected != null)
                selectionStore.enqueue { it + selected }
            selectedStore.update(null)
        }

        div {
            combobox<String> {
                items(itemsStore.data)
                value(selectedStore)
                selectionStrategy.manual()
                comboboxInput {}
                comboboxItems("bg-gray-300 rounded-md drop-shadow-lg") {
                    results.render { result ->
                        result.items.forEach { item ->
                            comboboxItem(item) {
                                div {
                                    className(active.map { if (it) "bg-black/20" else "" })
                                    +item.value
                                }
                            }
                        }
                    }
                }
            }
            div("mt-4") {
                + """Make a selection above and see it grow / loop.
                    | A bit of a strange use case, but the idea is, once an user makes a selection 
                    | it is added to a table and the combobox's input is cleared for the user to search another item.'
                    | The table is not implemented here, but you get the idea.
                """.trimMargin()
            }
            div("mt-4") {
                + "Selected into infinity:"
            }
            selectionStore.data.renderEach { item ->
                div { + item }
            }
        }
        portalRoot()
    }
}