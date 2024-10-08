import dev.fritz2.core.*
import dev.fritz2.headless.components.modal
import dev.fritz2.headless.foundation.portalRoot
import dev.fritz2.routing.routerOf
import kotlinx.browser.document
import kotlinx.coroutines.Job
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

fun customModal(modalOpen: Store<Boolean>, content: Tag<HTMLDivElement>.() -> Unit) {
    modal {
        openState(modalOpen)
        modalPanel {
            div("w-full fixed z-10 inset-0 overflow-y-auto") {
                div("flex items-end justify-center bg-black/50 min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0") {
                    span("hidden sm:inline-block sm:align-middle sm:h-screen") {
                        +" "
                    }
                    div(
                        """inline-block align-bottom sm:align-middle mx-auto px-6 py-3 bg-white shadow-md text-left overflow-hidden""".trimMargin()
                    ) {
                        div("m-4") {
                            a("text-blue-800") {
                                +"Close modal (Normal behaviour)"
                                clicks handledBy close
                            }
                        }

                        div("m-4") {
                            content()
                        }
                    }
                }
            }
        }
    }
}

fun RenderContext.page(store: Store<Boolean>?, name: String, goto: String) {
    val modalOpen = store ?: storeOf(false)
    div("h-full") {

        h1("m-4 font-bold text-xl") {
            +name
        }

        a("m-4 text-blue-800") {
            +"Goto $goto"
            href("#$goto")
        }

        a("m-4 text-blue-800") {
            +"Open modal"
            clicks handledBy {
                modalOpen.update(true)
            }
        }

        //Here we render a portal on every route change.
        //Local store (Render context Job) will clean up but the global store will not clean up and accumulating on the portal stack.
        customModal(modalOpen) {
            //A button to switch routes with the modal still open (can use the browsers back button too)
            //Only when using a local store (page1); global store will still work, but it suffers from accumulating on the portal stack.
            if (name == "page1") {
                a("text-blue-800") {
                    +"Goto $goto and break or navigate back using the browser"
                    href("#$goto")
                }
            }
        }

        p("m-4") {
            p("mt-4") {
                +"With a local and global store (page 1 and 2), when the route changes and the store is killed, the modal does not close."
            }
            p("mt-4") {
                +"With a global store (page 2), changing routes often, the modal accumulates on the portal stack and eventually displays black."
            }

            p("mt-4") {
                +"Click multiple times on Goto page1 / Goto page2, then on page2, click open modal. This demonstrates the global store issue."
            }

            p("mt-4") {
                +"Open modal on page1, then click on Goto page 2 and break. This demonstrates a store that was cancelled/stopped."
            }
        }
    }
}

val globalModalOpen = RootStore(false, Job())

fun main() {
    val mainDiv = document.getElementById("main") as? HTMLElement
    render(mainDiv!!) {
        val router = routerOf("page1")
        div("h-full") {
            router.data.render(this) { page ->
                when (page) {
                    //Local store
                    "page1" -> page(null, "page1", "page2")

                    //Global store, will accumulate on the portal stack.
                    "page2" -> page(globalModalOpen, "page2", "page1")
                    else -> +"No Page"
                }
            }
        }
        portalRoot()
    }
}