js;
/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{vue,js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        "tennis-green": "#90A955",
        "tennis-dark": "#31572C",
        "tennis-light": "#ECF39E",
      },
    },
  },
  plugins: [],
};
