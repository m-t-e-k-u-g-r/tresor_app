
export function evaluatePasswordStrength(password: string) {
    const rules = [
        /[0-9]/,
        /[a-z]/,
        /[A-Z]/,
        /[^0-9A-Za-z]/,
        /^.{8,32}$/,
    ];

    let passed = 0;
    for (const rule of rules) {
        if (rule.test(password)) {
            passed++;
        }
    }
    return Math.round((passed / rules.length) * 100);
}
