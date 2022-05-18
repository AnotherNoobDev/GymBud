package com.example.gymbud.model

enum class WorkoutBlockType {
    Rest, Warmup, Working
}

class WorkoutBlock (
    val block: BasicBlock,
    val type: WorkoutBlockType
    ) {
}

class WorkoutTemplate(
    override val id: ItemIdentifier,
    override var name: String
): Item {
    // list of SetTemplate * how many times to execute ( warmup + working sets) + RestBlocks
    // more flexibility if we just keep it as a list (for executing 3 times, add set template 3 times)
    private var _blocks: MutableList<WorkoutBlock> = mutableListOf()
    val blocks: List<WorkoutBlock>
        get() = _blocks.toList()


    fun addBlock(block: BasicBlock, type: WorkoutBlockType): WorkoutTemplate {
        _blocks.add(WorkoutBlock(block, type))
        return this
    }


    fun retrieveBlock(index: Int): WorkoutBlock {
        return _blocks[index]
    }


    fun removeBlock(index: Int) {
        _blocks.removeAt(index)
    }
}