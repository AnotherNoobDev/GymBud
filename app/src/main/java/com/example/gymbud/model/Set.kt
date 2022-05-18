package com.example.gymbud.model



// todo should this be a data class?
// double check what exactly it means to be a data class
data class SetTemplate(
    override val id: ItemIdentifier,
    override var name: String,
): Item, BasicBlock {
    private var _blocks: MutableList<BasicBlock> = mutableListOf()
    val blocks: List<BasicBlock>
        get() = _blocks.toList()


    fun addBlock(block: BasicBlock): SetTemplate {
        _blocks.add(block)
        return this
    }


    fun retrieveBlock(index: Int): BasicBlock {
        return _blocks[index]
    }


    fun removeBlock(index: Int) {
        _blocks.removeAt(index)
    }
}