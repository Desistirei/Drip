package br.com.stenox.cxc.utils.menu;

/*

A class extending the functionality of the regular Menu, but making it Paginated

This pagination system was made from Jer's code sample. <3

 */

public abstract class PaginatedMenu extends Menu {

    //Keep track of what page the menu is on
    protected int page = 0;
    //28 is max items because with the border set below,
    //28 empty slots are remaining.
    protected int maxItemsPerPage;
    //the index represents the index of the slot
    //that the loop is on
    protected int index = 0;

    public PaginatedMenu(PlayerMenuUtility playerMenuUtility, int maxItemsPerPage) {
        super(playerMenuUtility);
        this.maxItemsPerPage = maxItemsPerPage;
    }

    public PaginatedMenu(int maxItemsPerPage){
        this.maxItemsPerPage = maxItemsPerPage;
    }

    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }
}
