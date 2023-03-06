package demo.spy;

import java.util.List;

public class DirTreeVo {

    private String label;
    private String fullName;

    private List<DirTreeVo> children;

    public DirTreeVo() {
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<DirTreeVo> getChildren() {
        return children;
    }

    public void setChildren(List<DirTreeVo> children) {
        this.children = children;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}