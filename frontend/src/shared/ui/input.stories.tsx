import type { Meta, StoryObj } from "@storybook/react-vite";
import { useId } from "react";

import { Input } from "./input";

const meta = {
  title: "Shared/Input",
  component: Input,
  args: { placeholder: "Search employees", disabled: false },
  render: function LabelledInput(args) {
    const id = useId();
    return (
      <div className="flex max-w-sm flex-col gap-1.5">
        <label htmlFor={id} className="text-bodyStrong text-text-default">
          Employee code
        </label>
        <Input id={id} {...args} />
      </div>
    );
  },
} satisfies Meta<typeof Input>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Empty: Story = {};

export const Filled: Story = { args: { defaultValue: "EMP-00412" } };

export const Invalid: Story = {
  args: {
    defaultValue: "EMP-",
    "aria-invalid": true,
    "aria-describedby": "employee-code-error",
  },
  render: function InvalidInput(args) {
    const id = useId();
    return (
      <div className="flex max-w-sm flex-col gap-1.5">
        <label htmlFor={id} className="text-bodyStrong text-text-default">
          Employee code
        </label>
        <Input id={id} {...args} />
        <p
          id="employee-code-error"
          className="text-small text-status-danger-fg"
        >
          Enter the code as EMP- followed by five digits.
        </p>
      </div>
    );
  },
};

export const Disabled: Story = {
  args: { defaultValue: "EMP-00412", disabled: true },
};

export const Numeric: Story = {
  args: {
    type: "number",
    inputMode: "decimal",
    placeholder: "0.00",
    className: "text-right",
  },
};
