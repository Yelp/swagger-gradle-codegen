import io.swagger.codegen.CodegenModel
import io.swagger.codegen.CodegenProperty

internal enum class CodegenModelVar {
    ALL_VARS,
    OPTIONAL_VARS,
    PARENT_VARS,
    READ_ONLY_VARS,
    READ_WRITE_VARS,
    REQUIRED_VARS,
    VARS;

    companion object {
        /**
         * Allow the execution of an action on all the var attributes
         */
        fun forEachVarAttribute(
            codegenModel: CodegenModel,
            action: (CodegenModelVar, MutableList<CodegenProperty>) -> Unit
        ) {
            values().forEach {
                action(it, it.value(codegenModel))
            }
        }
    }

    internal fun value(codegenModel: CodegenModel): MutableList<CodegenProperty> {
        return when (this) {
            ALL_VARS -> codegenModel.allVars
            OPTIONAL_VARS -> codegenModel.optionalVars
            PARENT_VARS -> codegenModel.parentVars
            READ_ONLY_VARS -> codegenModel.readOnlyVars
            READ_WRITE_VARS -> codegenModel.readWriteVars
            REQUIRED_VARS -> codegenModel.requiredVars
            VARS -> codegenModel.vars
        }
    }
}
