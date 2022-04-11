package settlers.board;

public interface Vertex {

    // the vertices next to this one
    public Vertex[] getAdjacentVertices();

    public void setVertices(Vertex vertex, int position);

    // the edges next to this one
    public Edge[] getEdges();

}