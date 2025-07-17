using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BusinessObjects.ModerationModels.Blog
{
    public enum BlogPaidOptions
    {
        None,
        Option1,
        Option2,
        Option3
    }

    public static class BlogPaidOptionsExtensions
    {
        private static readonly Dictionary<BlogPaidOptions, (int Price, int Day, string Value)> _optionData = new()
        {
            { BlogPaidOptions.None, (0, 0, "0") },
            { BlogPaidOptions.Option1, (100, 1, "100") },
            { BlogPaidOptions.Option2, (300, 2, "300") },
            { BlogPaidOptions.Option3, (900, 3, "900") }
        };

        public static int GetPrice(this BlogPaidOptions option)
        {
            return _optionData.TryGetValue(option, out var data) ? data.Price : 0;
        }

        public static int GetDay(this BlogPaidOptions option)
        {
            return _optionData.TryGetValue(option, out var data) ? data.Day : 0;
        }

        public static string GetValue(this BlogPaidOptions option)
        {
            return _optionData.TryGetValue(option, out var data) ? data.Value : "0";
        }

        public static string GetDisplayText(this BlogPaidOptions option)
        {
            if (option == BlogPaidOptions.None)
                return "Không trả phí";

            var data = _optionData[option];
            return $"{data.Price} VND/ {data.Day} ngày";
        }
    }
}
