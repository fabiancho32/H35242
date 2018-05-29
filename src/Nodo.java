import java.util.List;

class Nodo {

        private String val;
        private int lavel;
        private Nodo parent;
        private List<Nodo> children;

        public Nodo(String val, Nodo parent, List<Nodo> children) {
            this.val = val;
            this.parent = parent;
            this.children = children;
            if(parent!=null)
                this.lavel = (int) parent.getLavel()+1;
            else 
                this.lavel = 0;
        }

        public String getVal() {
            return val;
        }
        public void setVal(String val) {
            this.val = val;
        }
        public Nodo getParent() {
            return parent;
        }
        public void setParent(Nodo parent) {
            this.parent = parent;
        }
        public List<Nodo> getChildren() {
            return children;
        }
        public void setChildren(List<Nodo> children) {
            this.children = children;
        }

        public int getLavel() {
            return lavel;
        }

        public void setLavel(int lavel) {
            this.lavel = lavel;
        }
    }

