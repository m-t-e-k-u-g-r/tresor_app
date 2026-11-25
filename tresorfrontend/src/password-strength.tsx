export default function PasswordStrength({ strength }: { strength: number }) {

    const getColor = () => {
        if (strength < 25) return "#ef4444";
        if (strength < 50) return "#f97316";
        if (strength < 75) return "#eab308";
        return "#22c55e";
    };

    return (
        <div style={{ width: "100%" }}>
            <div
                style={{
                    width: "100%",
                    height: "8px",
                    backgroundColor: "#e5e7eb",
                    borderRadius: "8px",
                    overflow: "hidden",
                }}
            >
                <div
                    style={{
                        width: `${strength}%`,
                        height: "100%",
                        backgroundColor: getColor(),
                        borderRadius: "8px",
                        transition: "width 0.3s ease, background-color 0.3s ease",
                    }}
                />
            </div>
        </div>
    );
}

