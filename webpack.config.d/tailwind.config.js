// must be in the jsMain/resource folder
const mainCssFile = 'styles.css';

// tailwind config (https://tailwindcss.com/docs/configuration)
const tailwind = {
    darkMode: ["class", ['selector', '.dark']],
    plugins: [
        require('@tailwindcss/forms')
    ],
    variants: {},
    content: {
        files: [
            '*.{js,html,css}',
            './kotlin/**/*.{js,html,css}',
            '!**/ace/*',
            '!*project-editor*',
        ],
        transform: {
            js: (content) => {
                return content.replaceAll(/(\\r)|(\\n)|(\\r\\n)/g, ' ')
            }
        }
    },
};


// webpack tailwind css settings
((config) => {
    let entry = '/kotlin/' + mainCssFile;
    if (config.entry && config.entry.main) {
        config.entry.main.push(entry);
    }
    config.module.rules.push({
        test: /\.css$/,
        use: [
            {loader: 'style-loader'},
            {loader: 'css-loader'},
            {
                loader: 'postcss-loader',
                options: {
                    postcssOptions: {
                        plugins: [
                            require("tailwindcss")({config: tailwind}),
                            require("autoprefixer"),
                            require("cssnano")
                        ]
                    }
                }
            }
        ]
    });
})(config);