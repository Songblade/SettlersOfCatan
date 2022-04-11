package settlers.board;

public interface Hex {

    /**
     *
     * @return a size 6 array containing the vertices
     */
    public Vertex[] getVertices();

    public void setVertex(Vertex vertex, int position);

    public boolean hasThief();

    public void setThief(boolean thiefIsHere);

}