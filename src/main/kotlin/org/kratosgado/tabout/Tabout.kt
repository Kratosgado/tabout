package org.kratosgado.tabout

import com.intellij.codeInsight.lookup.LookupManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import org.apache.commons.lang3.mutable.MutableInt

class Tabout: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
//        println("Tab triggered")
        val editor = e.getData(CommonDataKeys.EDITOR)
        if (editor != null ) {
            val isPopupOpened = LookupManager.getActiveLookup(editor) != null;

            val caret = editor.caretModel.currentCaret
            val document = editor.document
            val offset = MutableInt(caret.offset)

            when {
                isPopupOpened -> {
//                    println("completion popup is opened")
                    val actionHandler =
                        EditorActionManager.getInstance()
                            .getActionHandler(IdeActions.ACTION_EDITOR_TAB)
                    WriteCommandAction.runWriteCommandAction(editor.project) {
                        actionHandler.execute(editor, caret, e.dataContext)
                    }
                }
                 e.inputEvent.isShiftDown && shouldTabIn(document, offset)-> {
//                    println("Tabin")
                    caret.moveToOffset(offset.value)
                }
                offset.value < document.textLength && shouldTabout(document, offset) -> {
//                    println("Tabout")
                    caret.moveToOffset(offset.value)
                }
                else -> {
//                    println("Passing on")
                    val actionHandler =
                        EditorActionManager.getInstance()
                            .getActionHandler(IdeActions.ACTION_EDITOR_TAB)
                    WriteCommandAction.runWriteCommandAction(editor.project) {
                        actionHandler.execute(editor, caret, e.dataContext)
                    }
                }
            }
        }
    }

    private fun shouldTabout(document: Document, offset: MutableInt): Boolean {
        val endOffset = document.getLineEndOffset(document.getLineNumber(offset.value))
        val targets = charArrayOf(')', '}', ']', '(', '[', '{', '"', '\'')
        while (offset.value < endOffset){
            if (targets.contains(document.charsSequence[offset.andIncrement])) return true
        }
        return false
    }

    private  fun shouldTabIn(document: Document, offset: MutableInt): Boolean {
        val startOffset = document.getLineStartOffset(document.getLineNumber(offset.value))
        val targets = charArrayOf(')', '}', ']', '(', '[', '{', '"', '\'')
        while (offset.value > startOffset){
            if (targets.contains(document.charsSequence[offset.decrementAndGet()]) ) return true
        }
        return  false
    }
}