package enums;

public enum BoardConstant {

    ID("id"),
    NAME("name"),
    DESCRIPTION("desc"),
    CLOSED("closed");

    String constantName;

    BoardConstant(String constantName) {
        this.constantName = constantName;
    }

    public String getConstantName() {
        return constantName;
    }
}
